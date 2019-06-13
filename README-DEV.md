# DFLib Developer Guide

## Publishing the docs

```

mvn clean package -P docs
rsync -avz --delete dflib-doc/target/generated-docs/ ../dflib-docs/docs/
cd ../dflib-docs/docs/
git add -A
git commit -m 'Regenerating docs'
git push
```