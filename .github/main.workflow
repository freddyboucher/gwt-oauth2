workflow "Build/Deploy" {
  on = "push"
  resolves = ["Google Cloud SDK deploy"]
}

action "Maven build" {
  uses = "xlui/action-maven-cli/jdk8@master"
  args = "clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
}

action "If master branch" {
  needs = "Maven build"
  uses = "actions/bin/filter@master"
  args = "branch master"
}

action "Google Cloud SDK auth" {
  uses = "freddyboucher/gcloud/auth@master"
  needs = "If master branch"
  secrets = ["GCLOUD_AUTH"]
}

action "Google Cloud SDK deploy" {
  uses = "freddyboucher/gcloud/cli@master"
  needs = "Google Cloud SDK auth"
  args = "--quiet --verbosity=warning --project=gwt-oauth2 app deploy sample/sample-server/target/sample-server-0.4-SNAPSHOT --promote --version=0-4-snapshot"
}
