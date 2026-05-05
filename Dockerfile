FROM ubuntu:24.04

SHELL ["/bin/bash", "-c"]

# Install basic tools
RUN apt-get update
RUN apt-get install -yq --no-install-recommends \
    apt-transport-https \
    curl \
    git \
    gnupg \
    openjdk-11-jdk \
    python3 \
    python3-pip \
 && :

# Install sbt
# (See https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
RUN chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
RUN apt-get update
RUN apt-get install -yq --no-install-recommends sbt=1.9.7

# Clone and build Sirop compiler
RUN git clone \
    --single-branch \
    --depth 1 --shallow-submodules \
    --recurse-submodules \
    https://bitbucket.org/louishildebrand/sirop.git
WORKDIR /sirop
RUN ./githooks/post-commit # Create commit.txt
RUN sbt assembly

# Set up Python test scripts
WORKDIR /sirop/src/test/python
# TODO: This LaTeX installation is way too big; try installing texlive-base and adding only the required packages
RUN apt-get install -yq --no-install-recommends \
    texlive-latex-extra \
    cm-super \
 && :
RUN python3 -m pip install --break-system-packages -r requirements.txt
