# build this with nix build
{
  pkgs ? import <nixpkgs> { }
, pkgsLinux ? import <nixpkgs> { system="x86_64-linux"; }
}: 
let 
  my-bash = pkgs.buildEnv {
    name = "image-root";
    paths = [ pkgs.bash pkgs.coreutils ];
    pathsToLink = [ "/bin" ];
  };
in
pkgs.dockerTools.buildImage {
  name = "jcstress-arm";
  tag = "latest";

  copyToRoot = [
    my-bash
  ];

  config = {
    Cmd = [ "/bin/echo" "foo" ];
  };
}