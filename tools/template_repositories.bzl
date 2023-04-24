load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@io_machinecode_tools//:defs.bzl", "maven_repositories")

def template_repositories(
        st_version = "4.3.4",
        getopt_version = "1.0.13"):
    maven_install(
        name = "template_m2",
        repositories = maven_repositories,
        artifacts = [
            "gnu.getopt:java-getopt:" + getopt_version,
            "org.antlr:ST4:" + st_version,
        ],
    )
