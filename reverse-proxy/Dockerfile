FROM alpine:3.9

LABEL maintainer="Ryo Ota <nwtgck@nwtgck.org>"

RUN apk add --no-cache curl
RUN  curl -L https://github.com/mholt/caddy/releases/download/v0.11.5/caddy_v0.11.5_linux_amd64.tar.gz | tar zxf -

ENTRYPOINT [ "/caddy" ]
