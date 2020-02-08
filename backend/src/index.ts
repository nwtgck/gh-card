import * as yargs from "yargs";
import * as log4js from "log4js";
import * as redis from "redis";

import {RedisGitHubRepositoryJsonCacheRepository} from './infra/RedisGitHubRepositoryJsonCacheRepository';
import {DefaultGitHubApiService} from "./infra/DefaultGitHubApiService";
import {createServer} from "./route";

// Create option parser
const parser = yargs
  .option("redis-host", {
    describe: "Redis host",
    default: "localhost",
  })
  .option("github-client-id", {
    describe: "GitHub Client ID",
    type: "string",
  })
  .option("github-client-secret", {
    describe: "GitHub Client Secret",
    type: "string",
  });

// Create a logger
const logger = log4js.getLogger();
logger.level = "info";

// Parse arguments
const args = parser.parse(process.argv);
const redisHost: string = args["redis-host"];
const githubClientId: string | undefined = args["github-client-id"];
const githubSecret: string | undefined = args["github-client-secret"];

if (githubClientId === undefined) {
  logger.info("GitHub client ID is not set");
}

if (githubSecret === undefined) {
  logger.info("GitHub secret is not set");
}

const redisClient = redis.createClient({
  host: redisHost,
});

const gitHubRepositoryJsonCacheRepository = new RedisGitHubRepositoryJsonCacheRepository(
  redisClient,
  // 20 min
  // TODO: Hard code
  20 * 60
);
const gitHubApiService = new DefaultGitHubApiService(logger, gitHubRepositoryJsonCacheRepository);
const server = createServer({
  logger,
  gitHubApiService
});

// TODO: hard code
const httpPort = 8080
server.listen(httpPort, () =>{
  logger.info(`Listening on ${httpPort}...`);
});
