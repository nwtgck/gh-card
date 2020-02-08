import * as redis from 'redis';
import {promisify} from 'util';

import {GitHubRepositoryPngCardCacheRepository} from '../domain/GitHubRepositoryPngCardCacheRepository';

export class RedisGitHubRepositoryPngCardCacheRepository implements GitHubRepositoryPngCardCacheRepository {
  private keyPrefix: string = "repo-png-card";
  private redisGetAsync: (key: string) => Promise<string>;

  constructor(private readonly redisClient: redis.RedisClient, private readonly ttlSec: number) {
    this.redisGetAsync = promisify(this.redisClient.get).bind(this.redisClient);
  }

  cache(png: Buffer, repoName: string, useFullName: boolean, width: number, height: number): void {
    const key = `${this.keyPrefix}/${repoName}/fullname=${useFullName}/${width}x${height}`;
    // TODO: Not use base64 for performance
    this.redisClient.setex(key, this.ttlSec, png.toString("base64"));
  }
  
  async get(repoName: string, useFullName: boolean, width: number, height: number): Promise<Buffer | undefined> {
    const key = `${this.keyPrefix}/${repoName}/fullname=${useFullName}/${width}x${height}`;
    const pngBase64: string | null = await this.redisGetAsync(key);
    if (pngBase64 === null) return undefined;
    // TODO: Not use base64 for performance
    return Buffer.from(pngBase64, "base64");
  }
}
