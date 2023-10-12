# hla-18-peak-loadings GOAL!!!

Describe solution that solves peak loadings problem for biggest european football website https://goal.com

- Analyze all types of pages on the site
- Analyze and list possible sources of peak loadings
- Describe possible solution for each type

## Pages

1) Homepage (News)
2) Scores (matches schedule) - possible source of peak loadings, no live data here
3) Live match details - another possible source of peak loadings but live data is available here but it is not a simple way to find it. 
4) Static contents - Articles (Latest, Must read, Competitions, Lifestyle)

## Possible peak loadings sources
1) Bot activity (Crawlers, Scrapers)
2) Push notifications
3) Concurrent resources access
4) Live score polling
5) External attacks
6) Static data access(articles, images, etc.)

## Possible solutions

### Bot activity
1) Create robots.txt file and possibly to change it dynamically to stop bots from scrapping when peak loadings are encountered
2) Ban unwanted crawlers by header
### Push notifications
Solution here almost same as for Concurrent resources access. Since we when push notification will be sent - then we could 
scale up resources in advance.
### Concurrent resources access
1) Use AWS autoscale groups OR serverless calculations
2) Schedule scaling acording to matches schedule
3) Cache/invalidate cache for frequently accessed resources
### Live score polling
1) Use websocket to send live score updates to users instead of polling of the same page many times.
2) Make it more clear that the score/match data that is shown is live. For example, add a blinking icon or something like that.
### External attacks
Use protection tools like Cloudflare OR Amazon Shield of AWS
### Static data access(articles, images, etc.)
Use CDN + caching for static content and articles. Prerender static pages.