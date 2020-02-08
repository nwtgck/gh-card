import fetch from "node-fetch";
import { isLeft } from 'fp-ts/lib/Either';

import {GitHubApiService} from '../domain/GitHubApiService';
import {GithubRepoJson, githubRepoJsonType} from '../types';

export class DefaultGitHubApiService implements GitHubApiService {
  async getRepository(repoName: string): Promise<{repo: GithubRepoJson} | {status: number, resText: string}> {
    // TODO: Use GitHub Client ID and secret
    const githubRes = await fetch(`https://api.github.com/repos/${repoName}`);
    if (githubRes.status !== 200) {
      return {
        status: githubRes.status,
        resText: await githubRes.text(),
      };
    }
    const githubRepoJsonEither = githubRepoJsonType.decode(await githubRes.json());
    if (isLeft(githubRepoJsonEither)) {
      throw githubRepoJsonEither.left;
    }
    // TODO: cache
    const githubRepoJson = githubRepoJsonEither.right;
    return {
      repo: githubRepoJson,
    };
  }
}
