{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {

  buildInputs = [
    (pkgs.python3.withPackages(
      packages: with packages; [pandas lit filecheck click]
    ))
    pkgs.boogie
    pkgs.z3
  ];

  shellHook = ''
export PATH=$PATH:$PWD/byteback-tool/build/install/byteback-tool/bin
export CLASSPATH=$CLASSPATH:$PWD/byteback-annotations/build/libs/byteback-annotations.jar
  '';

}
