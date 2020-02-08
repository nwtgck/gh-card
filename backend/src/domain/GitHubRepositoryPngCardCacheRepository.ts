export interface GitHubRepositoryPngCardCacheRepository {
  cache(png: Buffer, repoName: string, useFullName: boolean, width: number, height: number): void;
  get(repoName: string, useFullName: boolean, width: number, height: number): Promise<Buffer | undefined>;
}
