FROM alpine:3.20

RUN apk add --no-cache \
		bash gcc gradle python3 py3-pip py3-pandas z3 dotnet6-sdk openjdk17

ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk"

RUN pip install --break-system-packages \
		click==8.1.3 lit==15.0.0 filecheck==0.0.22	 

RUN dotnet tool install -g \
		boogie --version 3.0.5.0

ENV PATH="$PATH:/root/.dotnet/tools"

ARG BYTEBACK_PATH /opt/byteback

ADD . /opt/byteback

WORKDIR "/opt/byteback"
RUN ./gradlew clean build
RUN ./gradlew install

ENV PATH="$PATH:/opt/byteback/byteback-core/build/install/byteback-core/bin"
