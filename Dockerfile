FROM ubuntu:24.04

SHELL ["/bin/bash", "-eux", "-o", "pipefail", "-c"]

COPY src/test/python/requirements.txt /requirements.txt
RUN <<EOF
# Install basic tools
apt-get update
apt-get install -yq --no-install-recommends \
    apt-transport-https \
    ca-certificates \
    curl \
    git \
    gnupg
# Install Java and sbt
# (See https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
apt-get update
apt-get install -yq --no-install-recommends \
    openjdk-11-jdk \
    sbt=1.9.7
# Install Python dependencies
# TODO: This LaTeX installation is way too big; try installing texlive-base and adding only the required packages
apt-get install -yq --no-install-recommends \
    python3 \
    python3-pip \
    texlive-latex-extra \
    cm-super
python3 -m pip install --break-system-packages -r /requirements.txt
EOF

# Clone and build Sirop compiler
# Change the echoed string to force re-cloning the repo
# TODO: It would probably be better to use docker COPY instead of clone
RUN echo flip > /dev/null \
 && git clone \
    --single-branch \
    --depth 1 --shallow-submodules \
    --recurse-submodules \
    https://bitbucket.org/louishildebrand/sirop.git
WORKDIR /sirop
# Run post-commit hook to create commit.txt, which is needed by build.sbt
# Also compile the tests and the SHIR tests so the user doesn't spend time waiting for it
RUN ./githooks/post-commit \
 && sbt assembly \
 && sbt 'Test/compile' \
 && cd /sirop/src/test/shir \
 && sbt 'Test/compile'

WORKDIR /sirop/src/test/python
