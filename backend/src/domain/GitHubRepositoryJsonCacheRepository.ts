export interface GitHubRepositoryJsonCacheRepository {
  cache(repoName: string, jsonString: string): void;
  get(repoName: string): Promise<string | undefined>
}
