import * as log4js from "log4js";
import fetch from "node-fetch";
import { isLeft } from 'fp-ts/lib/Either';

import {GitHubApiService} from '../domain/GitHubApiService';
import {GitHubRepositoryJsonCacheRepository} from '../domain/GitHubRepositoryJsonCacheRepository';
import {GithubRepoJson, githubRepoJsonType} from '../types';

export class DefaultGitHubApiService implements GitHubApiService {
  constructor(private readonly logger: log4js.Logger, private readonly gitHubRepositoryJsonCacheRepository: GitHubRepositoryJsonCacheRepository) {}

  async getRepository(repoName: string): Promise<{repo: GithubRepoJson} | {status: number, resText: string}> {
    let jsonStr: string | undefined = await this.gitHubRepositoryJsonCacheRepository.get(repoName);
    if (jsonStr === undefined) {
      this.logger.info(`Repository JSON ${repoName} is not cached`);
      // TODO: Use GitHub Client ID and secret
      const githubRes = await fetch(`https://api.github.com/repos/${repoName}`);
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
