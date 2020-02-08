import * as redis from 'redis';
import {promisify} from 'util';

import {GitHubRepositoryJsonCacheRepository} from '../domain/GitHubRepositoryJsonCacheRepository';

export class RedisGitHubRepositoryJsonCacheRepository implements GitHubRepositoryJsonCacheRepository {
  private keyPrefix = "repos-json";
  private redisGetAsync: (key: string) => Promise<string>;

  constructor(private readonly redisClient: redis.RedisClient, private readonly ttlSec: number) {
    this.redisGetAsync = promisify(this.redisClient.get).bind(this.redisClient);
  }

  cache(repoName: string, json: string): void {
    this.redisClient.setex(`${this.keyPrefix}/${repoName}`, this.ttlSec, json);
  }

  async get(repoName: string): Promise<string | undefined> {
    const jsonStr: string | null = await this.redisGetAsync(`${this.keyPrefix}/${repoName}`);
    return jsonStr ?? undefined;
  }
}
