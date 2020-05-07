import * as t from 'io-ts';


// NOTE: You can define fields more if need
//       (see: https://developer.github.com/v3/repos/#response-5)
export const githubRepoJsonType = t.type({
  description: t.union([t.string, t.undefined, t.null]),
  language: t.union([t.string, t.null]),
  stargazers_count: t.number,
  forks_count: t.number,
  fork: t.boolean,
});
export type GithubRepoJson = t.TypeOf<typeof githubRepoJsonType>;
