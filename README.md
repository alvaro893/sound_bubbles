# Sound bubbles


## Pushing changes to this Repository
- Never push directly to the master
- Create a pull request (PR)

Hence you first make a branch:
- `git checkout -b your-branch-name`
- add the changed files `git add --all` for all files
- `git commit -m 'my commit message'`
-  Before push download the last changes with: `git pull origin master`
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

## Testing in Android studio
- add `testCompile 'junit:junit:4.12'` to "dependencies" in build.gradle (Module:app)
- Create folder `app/src/test/java` if needed

#### To create a testing class
- Open your class
- In the editor (not in the class name) right-click and select `Go To` -> `Tests`
- Follow the stepts to create the test

#### See the [documentation](https://developer.android.com/training/testing/unit-testing/local-unit-tests.html#run)
