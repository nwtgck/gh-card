import * as t from 'io-ts';
import { isLeft } from 'fp-ts/lib/Either'
import * as express from 'express';
import * as log4js from "log4js";
import fetch from "node-fetch";

const repoWithExtType = t.type({
  ownerName: t.string,
  shortRepoName: t.string,
  extension: t.string
});
type RepoWithExt = t.TypeOf<typeof repoWithExtType>;

// TODO: Extract as something
const githubRepoJsonType = t.type({
  description: t.union([t.string, t.undefined]),
  language: t.union([t.string, t.undefined]),
  stargazers_count: t.number,
  forks_count: t.number,
});
type GithubRepoJson = t.TypeOf<typeof githubRepoJsonType>;

export function createServer(logger: log4js.Logger) {
  const app = express();

  app.get('/', (req, res) => {
    res.send("e.g. /repos/rust-lang/rust.svg");
  });
  
  app.get('/repos/:ownerName/:shortRepoName.:extension', async (req, res) => {
    const repoWithExtEither = repoWithExtType.decode(req.params);
    if (isLeft(repoWithExtEither)) {
      res.status(400);
      res.send('invalid request');
      return;
    }
    const repoWithExt = repoWithExtEither.right;
    logger.info(`valid request: ${JSON.stringify(repoWithExt)}`);
    const {ownerName, shortRepoName, extension} = repoWithExt;

    // TODO: Extract as something
    // TODO: Use GitHub Client ID and secret
    const githubRes = await fetch(`https://api.github.com/repos/${ownerName}/${shortRepoName}`);
    const githubRepoJsonEither = githubRepoJsonType.decode(await githubRes.json());
    if (isLeft(githubRepoJsonEither)) {
      res.status(500);
      res.send("Error in GitHub API");
      logger.error(`Invalid JSON from GitHub API: ${JSON.stringify(repoWithExt)}`);
      return;
    }
    const githubRepoJson = githubRepoJsonEither.right;
    // TODO: remove
    console.log(githubRepoJson);

    // TODO: implement
    res.send(req.params);
  });

  return app;
}
