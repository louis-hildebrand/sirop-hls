FROM ubuntu:24.04

SHELL ["/bin/bash", "-eux", "-o", "pipefail", "-c"]

RUN --mount=type=bind,src=src/test/python/requirements.txt,dst=/requirements.txt <<EOF
# Install basic tools
apt-get update
apt-get install -yq --no-install-recommends \
    apt-transport-https \
    ca-certificates \
    cloc \
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
apt-get install -yq --no-install-recommends \
    python3 \
    python3-pip
python3 -m pip install --break-system-packages -r /requirements.txt
EOF

# TODO: This LaTeX installation is way too big; try installing texlive-base and adding only the required packages
RUN <<EOF
# Install LaTeX so I can generate plots
apt-get update
apt-get install -yq --no-install-recommends \
    texlive-latex-extra \
    cm-super
EOF

# Copy over Sirop, Aetherling, and SHIR compilers
ADD https://github.com/chipsalliance/chisel.git#v3.2.8 /chisel
COPY --parents \
     --exclude=target/ \
     --exclude=__pycache__/ \
     ./lib/ \
     ./project/build.properties \
     ./project/plugins.sbt \
     ./src/main/ \
     ./src/test/aetherling/src/ \
     ./src/test/aetherling/chiselAetherling/ \
     ./src/test/java/ \
     ./src/test/python/ \
     ./src/test/resources/aetherling_benchmarks/*.csv \
     ./src/test/resources/aetherling_benchmarks/original/ \
     ./src/test/resources/aetherling_benchmarks/verilog/ \
     ./src/test/resources/shir_benchmarks/ \
     ./src/test/scala/ \
     ./src/test/sh/ \
     ./src/test/shir/ \
     ./build.sbt \
     /sirop
WORKDIR /sirop
# Compile the tests and the SHIR compiler so the user doesn't spend time waiting for it.
# It adds 100-200 MB to the image size, but saves the user a minute or two.
# It also has the minor benefit that we confirm compilation succeeds when building the docker
# image; if there is a problem, we catch it immediately rather than when the user tries running
# the container.
RUN sbt assembly \
 && sbt 'Test/compile' \
 && cd /sirop/src/test/shir \
 && sbt 'Test/compile'

WORKDIR /sirop/src/test/python
