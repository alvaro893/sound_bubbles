# Sound bubbles

## The App
### Playing with sounds
The user can play the sounds draging them as bubbles
![image](screenshots/Screenshot_2015-12-13-00-25-48.png)
### Recording sounds
![image](screenshots/Screenshot_2015-12-13-00-25-56.png)
### Search files from Museo database
![image](screenshots/Screenshot_2015-12-13-00-30-02.png)
### More screenshoots
![image](screenshots/Screenshot_2015-12-13-00-27-38.png)

## Pushing changes to this Repository
- Never push directly to the master
- Create a pull request (PR)

Hence you first make a branch:
- `git checkout -b your-branch-name`
- add the changed files `git add --all` for all files
- `git commit -m 'my commit message'`
-  Before push download the last changes with: `git fetch && git rebase origin/master` or `git pull origin master`
- `git push origin your-branch-name`
After that, go to GitHub:
- review your code and if needed make changes locally and push again
- find your new branch and click 'create pull request'
- Preferably let someone else merge this branch to master
- `git checkout master`
- `git pull origin master`

### some useful commands:
- `git checkout <<branch-or-commit>>` it changes the project to a past commit or to a branch (either local or remote).
- `git log` to see information about commits
- `git status` all information about your non committed changes
- `git reset --hard HEAD` destroy all changes. USE CAREFULLY
### last advice: use [Google](www.google.com) :)
