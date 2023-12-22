FROM archlinux:latest

RUN pacman -Syu --noconfirm
	
RUN pacman -S --noconfirm \
		bash gcc gradle python-pip python-pipx dotnet-sdk-6.0 jdk17-openjdk

RUN pip install --break-system-packages \
		pandas==1.4.3 click==8.1.3 z3-solver==4.11.2 lit==15.0.0 filecheck==0.0.22	 

RUN dotnet tool install -g \
		boogie --version 2.15.0

ENV PATH="$PATH:/root/.dotnet/tools"

ARG BYTEBACK_PATH /opt/byteback

ADD . /opt/byteback

WORKDIR "/opt/byteback"
RUN ./gradlew clean build
RUN ./gradlew install

ENV PATH="$PATH:/opt/byteback/byteback-core/build/install/byteback-core/bin"