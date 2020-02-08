# NOTE: Multi-stage Build

FROM node:10 as build

# Copy all things in this repo except files in .dockerignore
COPY . /app
# Move to /app
WORKDIR /app

# Install requirements, build and remove devDependencies
# (from: https://stackoverflow.com/a/25571391/2885946)
RUN cd /app && \
    npm install && \
    npm run build && \
    npm prune --production


# Make directories
RUN mkdir -p /app/backend

# TODO: Use more proper image
# Python3
FROM python:3
LABEL maintainer="Ryo Ota <nwtgck@gmail.com>"

# Make directories
RUN mkdir -p /app

# Copy artifact
COPY --from=build /app/dist /app/frontend

WORKDIR /app/frontend

# Run entry (Run the server)
ENTRYPOINT ["python3", "-m", "http.server"]
