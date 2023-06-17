# lab 3

# Machine Learning - Decision Tree Classification

This lab focuses on classification using supervised machine learning.

A supervised model first needs to be trained on labeled data, and after that the model can be used for prediction (
classification or regression) on previously unseen data.

Data instances are represented as vector of features x, where each feature encodes some characteristic of the instance.
The label y is a discrete value for classification or a continuous value for regression. After training, the model can
be used to predict the label of a new instance.

The main goal of training the model is to generalize well on unseen data.

"If the model is too complex, it will adapt too much to the data it has
been trained on, but give poor predictions on unseen data ⇒
overfitting"

Decision trees are represented using a tree structure, where each node represents a feature, each branch a decision
rule, and each leaf a label.
The decision tree is built using the ID3 algorithm.
To prevent overfitting, the max depth of the tree is also implemented.

#### ID3 algorithm

```
function id3(D, Dparent, X, y)      -- initially Dparent = D
    if D = ∅ then
        v ← argmax_v|D^parent_y=v | -- most frequent label of parent node
        return Leaf(v)
    v ← argmax_v|D_y=v|             -- most frequent label of current node
    if X = ∅ or D = D_y=v then
        return Leaf(v)
    x ← argmax_x∈X IG(D, x)         -- most discriminative feature
    subtrees ← ∅
    for v ∈ V (x):
        t ← id3(D_x=v, D, X \ {x}, y)
        subtrees ← append(subtrees,(v, t))
    return Node(x, subtrees)

X – set of all features, y – class label, D – set of labeled examples
```
