def _st_java_library(ctx):
    name = ctx.label.name

    classpath = ""
    add = False
    for file in ctx.files._classpath:
        if add:
            classpath += ":"
        add = True
        classpath += file.path

    command = """
        set -euo pipefail;
        mkdir -p {target} \
            && java -cp {classpath} io.machinecode.vial.build.Main \
                   -i $(find . -name st | grep -v bazel-out) \
                   -o {target} \
            && jar cf {jar_name} -C {target} .
    """.format(
        jar_name = ctx.outputs.jar.path,
        classpath = classpath,
        target = name,
    )
    outs = [ctx.outputs.jar]
    ctx.actions.run_shell(
        inputs = ctx.files.srcs + ctx.files._classpath,
        outputs = outs,
        arguments = [],
        command = command,
    )
    return struct(files = depset(outs))

st_java_library = rule(
    implementation = _st_java_library,
    output_to_genfiles = True,
    attrs = {
        "_classpath": attr.label_list(default = [
            Label("@template_m2//:gnu_getopt_java_getopt"),
            Label("@template_m2//:org_antlr_ST4"),
            Label("@template_m2//:org_antlr_antlr_runtime"),
            Label("//build/src/main/java/io/machinecode/vial/build"),
        ]),
        "srcs": attr.label_list(),
    },
    outputs = {
        "jar": "%{name}.srcjar",
    },
)
"""Generate code from st templates.

Args:
  srcs: The template files.
"""
