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

}
