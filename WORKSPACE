workspace(name = "io_machinecode_vial")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

#local_repository(
#    name = "io_machinecode_tools",
#    path = "../tools",
#)

io_machinecode_tools_version = "a63b1a3c63bf84df90109bf3f4d2f3c91415f4fb"

http_archive(
    name = "io_machinecode_tools",
    sha256 = "24b378850e3d1b322aaefa98ea676b77d964b9f5fa342d837686a52de276a5ef",
    strip_prefix = "tools-" + io_machinecode_tools_version,
    urls = [
        "https://mirror.bazel.build/github.com/BrentDouglas/tools/archive/%s.tar.gz" % io_machinecode_tools_version,
        "https://github.com/BrentDouglas/tools/archive/%s.tar.gz" % io_machinecode_tools_version,
    ],
)

load("@io_machinecode_tools//imports:java_repositories.bzl", "java_repositories")

java_repositories()

load("@io_machinecode_tools//tools/java:devserver.bzl", "devserver")

devserver(
    name = "io_machinecode_devserver",
    hosts = [
        "localhost",
        "0.0.0.0",
    ],
)

load("@io_machinecode_tools//imports:stardoc_repositories.bzl", "stardoc_repositories")

stardoc_repositories()

load("@io_machinecode_tools//imports:nodejs_repositories.bzl", "nodejs_repositories")

nodejs_repositories()

load("@build_bazel_rules_nodejs//:repositories.bzl", "build_bazel_rules_nodejs_dependencies")

build_bazel_rules_nodejs_dependencies()

load("@io_machinecode_tools//imports:nodejs_binary_repositories.bzl", "nodejs_binary_repositories")

nodejs_binary_repositories()

load("@build_bazel_rules_nodejs//:index.bzl", "yarn_install")

yarn_install(
    name = "npm",
    package_json = "//:package.json",
    yarn_lock = "//:yarn.lock",
)

load("@io_bazel_rules_sass//:defs.bzl", "sass_repositories")

sass_repositories()

load("@io_bazel_stardoc//:setup.bzl", "stardoc_repositories")

stardoc_repositories()

load("@io_machinecode_tools//imports:build_repositories.bzl", "build_repositories")

build_repositories()

load("@io_machinecode_tools//imports:checkstyle_repositories.bzl", "checkstyle_repositories")

checkstyle_repositories()

load("@io_machinecode_tools//imports:format_repositories.bzl", "format_repositories")

format_repositories()

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")

protobuf_deps()

load("@io_machinecode_tools//imports:go_repositories.bzl", "go_repositories")

go_repositories()

load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")

go_rules_dependencies()

go_register_toolchains(version = "1.20.2")

load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies")

gazelle_dependencies()

load("@io_machinecode_tools//imports:devsrv_repositories.bzl", "devsrv_repositories")

devsrv_repositories()

load("@io_machinecode_tools//imports:jmh_repositories.bzl", "jmh_repositories")

jmh_repositories()

load("//tools:template_repositories.bzl", "template_repositories")

template_repositories()

load("@io_machinecode_tools//imports:distribution_repositories.bzl", "distribution_repositories")

distribution_repositories()

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories")

kotlin_repositories()

load("@io_bazel_rules_kotlin//kotlin:core.bzl", "kt_register_toolchains")

kt_register_toolchains()

load("@vaticle_bazel_distribution//maven:deps.bzl", "maven_artifacts_with_versions")

# 31.1-jre
guava_testlib_version = "17.0"

load("@io_machinecode_tools//:defs.bzl", "maven_repositories")
load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

maven_install(
    name = "m2",
    artifacts = [
        "com.google.guava:guava-testlib:" + guava_testlib_version,
    ],
    fetch_sources = True,
    repositories = maven_repositories,
)

maven_install(
    name = "maven",
    artifacts = maven_artifacts_with_versions,
    fetch_sources = True,
    repositories = maven_repositories,
)

maven_install(
    name = "perf_m2",
    artifacts = [
        "com.goldmansachs:gs-collections-api:7.0.3",
        "com.goldmansachs:gs-collections:7.0.3",
        "com.carrotsearch:hppc:0.9.1",
        "it.unimi.dsi:fastutil:8.5.12",
        "com.koloboke:koloboke-api-jdk8:1.0.0",
        "com.koloboke:koloboke-impl-jdk8:1.0.0",
        "net.sf.trove4j:trove4j:3.0.3",
        "org.openjdk.jmh:jmh-core:1.36",
        "org.openjdk.jmh:jmh-generator-annprocess:1.36",
        "com.google.caliper:caliper:1.0-beta-3",
        "org.openjdk.jol:jol-core:0.17",
        "junit:junit:4.12",
    ],
    fetch_sources = True,
    repositories = maven_repositories,
)
