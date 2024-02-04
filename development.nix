{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {

  buildInputs = [
    pkgs.openjdk17
    (pkgs.python3.withPackages(
      packages: with packages; [pandas lit filecheck click]
    ))
    pkgs.boogie
    pkgs.z3
  ];

  shellHook = ''
export PATH=$PATH:$PWD/byteback-cli/build/install/byteback-cli/bin
export CLASSPATH=$CLASSPATH:$PWD/byteback-annotations/build/libs/byteback-annotations.jar
  '';

}
