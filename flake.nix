{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  outputs = { nixpkgs, ... }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" ];
      root = ./.;
    in {
      packages = nixpkgs.lib.genAttrs systems (system:
        let
          pkgs = import nixpkgs { inherit system; };

          imageRoot = pkgs.buildEnv {
            name = "image-root";
            paths = [ pkgs.bash pkgs.coreutils ];
            pathsToLink = [ "/bin" ];
          };
        in {
          default = pkgs.dockerTools.buildImage {
            name = "jcstress-arm";
            tag = "latest";

            copyToRoot = [ imageRoot "${root}/poop.txt" ];

            config.Cmd = [ "/bin/echo" "foo" ];
          };
        });
    };
}