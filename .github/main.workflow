workflow "Build" {
  on = "push"
  resolves = ["Maven build"]
}

action "Maven build" {
  uses = "xlui/action-maven-cli/jdk8@master"
  args = "clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
}