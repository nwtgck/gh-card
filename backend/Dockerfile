FROM node:12.14

LABEL maintainer="Ryo Ota <nwtgck@nwtgck.org>"

COPY . /app

# Move to /app
WORKDIR /app

# Install requirements, build and remove devDependencies
# (from: https://stackoverflow.com/a/25571391/2885946)
RUN npm ci && \
    npm run build && \
    npm prune --production

# Run a server
ENTRYPOINT [ "node", "dist/src/index.js" ]
