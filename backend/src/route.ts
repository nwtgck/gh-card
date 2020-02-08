import * as t from 'io-ts';
import { isLeft } from 'fp-ts/lib/Either'
import * as express from 'express';
import * as log4js from "log4js";

const repoWithExtType = t.type({
  owner: t.string,
  repo: t.string,
  ext: t.string
});
type RepoWithExt = t.TypeOf<typeof repoWithExtType>;

export function createServer(logger: log4js.Logger) {
  const app = express();

  app.get('/', (req, res) => {
    res.send("e.g. /repos/rust-lang/rust.svg");
  });
  
  app.get('/repos/:owner/:repo.:ext', (req, res) => {
    const repoWithExtEither = repoWithExtType.decode(req.params);
    if (isLeft(repoWithExtEither)) {
      res.status(400);
      res.send('invalid request');
      return;
    }
    const repoWithExt: RepoWithExt = repoWithExtEither.right;
    logger.info(`valid request: ${JSON.stringify(repoWithExt)}`);
    // TODO: implement
    res.send(req.params);
  });

  return app;
}
