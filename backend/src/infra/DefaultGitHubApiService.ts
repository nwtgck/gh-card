import * as log4js from "log4js";
import fetch from "node-fetch";
import { isLeft } from 'fp-ts/lib/Either';

import {GitHubApiService} from '../domain/GitHubApiService';
import {GitHubRepositoryJsonCacheRepository} from '../domain/GitHubRepositoryJsonCacheRepository';
import {GithubRepoJson, githubRepoJsonType} from '../types';

export class DefaultGitHubApiService implements GitHubApiService {
  constructor(
    private readonly logger: log4js.Logger,
    private readonly githubCredential: {githubClientId: string, githubClientSecret: string} | undefined,
    private readonly gitHubRepositoryJsonCacheRepository: GitHubRepositoryJsonCacheRepository) {}

  async getRepository(repoName: string): Promise<{repo: GithubRepoJson} | {status: number, resText: string}> {
    let jsonStr: string | undefined = await this.gitHubRepositoryJsonCacheRepository.get(repoName);
    if (jsonStr === undefined) {
      this.logger.info(`Repository JSON ${repoName} is not cached`);

      let query = '';
      const headers= (() => {
        if (this.githubCredential === undefined) {
          return {};
        } else {
          const {githubClientId, githubClientSecret} = this.githubCredential;
          const h: {[key: string]: string} = {
            // Basic Auth
            "Authorization": `Basic: ${Buffer.from(`${githubClientId}:${githubClientSecret}`).toString("base64")}`,
          };
          query = `?client_id=${githubClientId}&client_secret=${githubClientSecret}`;
          return h;
        }
      })();

      const githubRes = await fetch(`https://api.github.com/repos/${repoName}${query}`, {
        headers,
      });
      if (githubRes.status !== 200) {
        return {
          status: githubRes.status,
          resText: await githubRes.text(),
        };
      }
      jsonStr = await githubRes.text();
      // Cache
      this.gitHubRepositoryJsonCacheRepository.cache(repoName, jsonStr);
    }

    const githubRepoJsonEither = githubRepoJsonType.decode(JSON.parse(jsonStr));
    if (isLeft(githubRepoJsonEither)) {
      throw githubRepoJsonEither.left;
    }
    const githubRepoJson = githubRepoJsonEither.right;
    return {
      repo: githubRepoJson,
    };
  }
}
