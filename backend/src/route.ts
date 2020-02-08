import * as t from 'io-ts';
import { isLeft } from 'fp-ts/lib/Either'
import * as express from 'express';
import * as log4js from "log4js";
import fetch from "node-fetch";
import {renderToString} from 'react-dom/server';

import {generateSvg} from './svg-generator';


const repoWithExtType = t.type({
  ownerName: t.string,
  shortRepoName: t.string,
  extension: t.string
});
type RepoWithExt = t.TypeOf<typeof repoWithExtType>;

const repoRequestQueryType = t.type({
  fullname: t.union([t.string, t.undefined]),
  link_target: t.union([t.string, t.undefined]),
});

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

    // Validate query
    const repoRequestQueryEither = repoRequestQueryType.decode(req.query);
    if (isLeft(repoRequestQueryEither)) {
      res.status(400);
      res.send('invalid query parameter');
      return;
    }
    const repoRequestQuery = repoRequestQueryEither.right;
    const usesFullName: boolean = repoRequestQuery.fullname !== undefined;
    const linkTarget: string = repoRequestQuery.link_target ?? "";

    // TODO: Extract as something
    // TODO: Use GitHub Client ID and secret
    const githubRes = await fetch(`https://api.github.com/repos/${ownerName}/${shortRepoName}`);
    // TODO: handle repository not found
    const githubRepoJsonEither = githubRepoJsonType.decode(await githubRes.json());
    if (isLeft(githubRepoJsonEither)) {
      res.status(500);
      res.send("Error in GitHub API");
      logger.error(`Invalid JSON from GitHub API: ${JSON.stringify(repoWithExt)}`);
      return;
    }
    // TODO: cache
    const githubRepoJson = githubRepoJsonEither.right;

    // TODO: handle .png
    const svg = generateSvg({
      ownerName,
      shortRepoName,
      usesFullName,
      linkTarget,
      language: githubRepoJson.language,
      description: githubRepoJson.description ?? "",
      nStars: githubRepoJson.stargazers_count,
      nForks: githubRepoJson.forks_count,
    });

    const svgString: string = renderToString(svg);

    res.header({
      'Content-Type': 'image/svg+xml',
    });
    res.send(svgString);
  });

  return app;
}
