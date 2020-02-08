import * as t from 'io-ts';


// NOTE: You can define fields more if need
//       (see: https://developer.github.com/v3/repos/#response-5)
export const githubRepoJsonType = t.type({
  description: t.union([t.string, t.undefined]),
  language: t.union([t.string, t.undefined]),
  stargazers_count: t.number,
  forks_count: t.number,
});
export type GithubRepoJson = t.TypeOf<typeof githubRepoJsonType>;
