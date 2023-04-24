build_deps = []

test_deps = [
    "@m2//:junit_junit",
    "@m2//:com_google_guava_guava_testlib",
]

jmh_deps = [
    "@jmh_m2//:org_openjdk_jmh_jmh_core",
    "@jmh_m2//:net_sf_jopt_simple_jopt_simple",
    "@jmh_m2//:org_apache_commons_commons_math3",
]
