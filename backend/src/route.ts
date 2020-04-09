import * as t from 'io-ts';
import { isLeft } from 'fp-ts/lib/Either';
import * as express from 'express';
import * as log4js from "log4js";
import {renderToString} from 'react-dom/server';
import * as svg2png from 'svg2png';

import {GitHubApiService} from './domain/GitHubApiService';
import {GitHubRepositoryPngCardCacheRepository} from './domain/GitHubRepositoryPngCardCacheRepository';
import {generateSvg} from './svg-generator';


const repoWithExtType = t.type({
  ownerName: t.string,
  shortRepoName: t.string,
  extension: t.union([t.literal("svg"), t.literal("png")]),
});
type RepoWithExt = t.TypeOf<typeof repoWithExtType>;

const repoRequestQueryType = t.type({
  fullname: t.union([t.string, t.undefined]),
  link_target: t.union([t.string, t.undefined]),
});

export function createServer(
  {logger, gitHubApiService, gitHubRepositoryPngCardCacheRepository}:
  {logger: log4js.Logger, gitHubApiService: GitHubApiService, gitHubRepositoryPngCardCacheRepository: GitHubRepositoryPngCardCacheRepository}) {
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

    const repoName = `${ownerName}/${shortRepoName}`;
    const repoResult = await gitHubApiService.getRepository(`${repoName}`);

    if ("status" in repoResult) {
      logger.error(`GitHub API error: ${JSON.stringify(repoResult)}`);
      if (repoResult.status === 404) {
        res.status(404);
        res.send(`${repoName} not found\n`);
        return;
      }
      res.status(400);
      res.send("Error in GitHub API\n");
      return;
    }
    const githubRepoJson = repoResult.repo;

    const {width, height, svg} = generateSvg({
      ownerName,
      shortRepoName,
      usesFullName,
      linkTarget,
      language: githubRepoJson.language ?? undefined,
      description: githubRepoJson.description ?? "",
      nStars: githubRepoJson.stargazers_count,
      nForks: githubRepoJson.forks_count,
      isFork: githubRepoJson.fork,
    });
    const svgStr: string = renderToString(svg);

    switch (extension) {
      case "svg":
        res.header({
          'Content-Type': 'image/svg+xml',
        });
        res.send(svgStr);
        return;
      case "png":
        let pngBuffer: Buffer | undefined = await gitHubRepositoryPngCardCacheRepository.get(
          repoName,
          usesFullName,
          width,
          height,
        );
        if (pngBuffer === undefined) {
          // TODO: High resolution
          pngBuffer = await svg2png(
            Buffer.from(svgStr),
            {width, height}
          );
          // Cache PNG
          gitHubRepositoryPngCardCacheRepository.cache(
            pngBuffer,
            repoName,
            usesFullName,
            width,
            height,
          );
        }

        res.header({
          'Content-Type': 'image/png',
        });
        res.send(pngBuffer);
        return;
    }
  });

  return app;
}
