# gh-card
[![CircleCI](https://circleci.com/gh/nwtgck/gh-card.svg?style=shield)](https://circleci.com/gh/nwtgck/gh-card)

GitHub Repository Card for Every Web Site: <https://gh-card.dev>

## Example SVG card

![Piping Server static repo card](doc_assets/piping-server.svg)  

## Demo
![gh-card](doc_assets/gh-card.gif)

## How it works?
The idea is similar to status badges from Travis CI, CircleCI and etc.

An image URL is like "<https://gh-card.dev/repos/nwtgck/piping-server.svg>". The request triggers the backend server to call GitHub API request. The repository information are cached currently by Redis.


## Related projects
* [GitHub Link Card Creator](https://github.com/po3rin/github_link_creator)
* [Unofficial GitHub Cards](https://github.com/lepture/github-cards)

This might be similar to them. The purpose of this project is to provide an image which can be used in every site and the design should be like official GitHub repo card. I hope one day GitHub itself provides this feature officially.
