ROOT_DIR = .
include params.mk

.PHONY: help
help:
	@echo ""
	@echo "-- Available make targets:"
	@echo ""
	@echo "   build                      - Build the library"
	@echo "   format                     - Format the code"
	@echo "   check                      - Run linters"
	@echo "   test                       - Run the tests"
	@echo "   list-bench                 - Print benchmark targets"
	@echo "   bench                 	 - Run a benchmark with param target=<bench>"
	@echo "   coverage                   - Get test coverage"
	@echo "   doc                        - Build the docs"
	@echo "   site                       - Build and watch the website"
	@echo ""


.PHONY: all
all: build check tools #build-coverage doc

.PHONY: build
build:
	@bazel build //core/src/main/java/io/machinecode/vial \
		$(args)

.PHONY: format
format:
	@bazel build @com_github_bazelbuild_buildtools//buildifier \
				@google_java_format//jar
	@find . -type f \( -name BUILD -or -name BUILD.bazel \) \
		| bazel run //:buildifier
	@java -jar \
		--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
		$$(bazel info output_base)/external/google_java_format/jar/downloaded.jar -i \
		$$(find core/src bench build/src -type f -name '*.java')

.PHONY: check
check:
	@bazel test //...:all \
		--build_tag_filters=check \
		--test_tag_filters=check \
		$(args)

.PHONY: test
test:
	@bazel test //...:all \
		--test_tag_filters=-check \
		$(args)

.PHONY: list-bench
list-bench:
	@bazel query "kind(java_binary, //bench/perf/...)"

.PHONY: bench
bench:
	@bazel build $(target)_deploy.jar
	@java -jar $$(bazel cquery --ui_event_filters=-info --noshow_progress $(target)_deploy.jar --output files)

.PHONY: build-coverage
build-coverage:
	@if [ -e bazel-out ]; then find bazel-out -name coverage.dat -exec rm {} +; fi
	@bazel coverage \
		//core/src/main/java/io/machinecode/vial/...:all \
		//core/src/test/java/io/machinecode/vial/...:all \
		--test_tag_filters=-check \
		$(args)
	@bazel build //:coverage \
		$(args)

.PHONY: coverage
coverage: build-coverage
	@mkdir -p .srv/cov
	@rm -rf .srv/cov && mkdir -p .srv/cov
	@bash -c "(cd .srv/cov && tar xf $(BAZEL_BIN)/coverage.tar)"
	@$(open) .srv/cov/index.html

.PHONY: tools
tools:
	@bazel build \
		@io_machinecode_devserver//:devserver \
		@io_machinecode_tools//tools:watch

.PHONY: doc
doc:
	@bazel build //:site \
		$(args)

.PHONY: run-site
run-site:
	$(DEV_SRV) \
		--debug=$(SITE_DEVSRV_DEBUG_PORT) \
		--dir .srv/site \
		--host $(host) \
		--port $(SITE_PORT) \
		--push-resources /,/index.html=/css/$(shell bash -c "cd .srv/site/css && find *.css"),/logo.svg,/favicon.ico \
		$(keystore) \
		$(args)

.PHONY: serve-site
serve-site: doc
	@mkdir -p .srv/site
	@rm -rf .srv/site && mkdir -p .srv/site
	@bash -c "(cd .srv/site && tar xf $(BAZEL_BIN)/site.tar)"
	@curl -fs $(transport)://$(host):$(SITE_PORT)/notify || $(open) $(transport)://$(host):$(SITE_PORT)/

.PHONY: site
site: tools
	@$(WATCH) \
		-d src/main/site \
		-c 'make run-site' \
		-w 'make serve-site'

.PHONY: deploy
deploy:
	@bazel run //:deploy -- $$(bash -c "if grep SNAPSHOT version.txt >/dev/null; then echo snapshot; else echo release --gpg; fi")