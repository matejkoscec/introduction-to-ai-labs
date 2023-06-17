# lab 2

# Knowledge Representation using Formal Logic, Automated Reasoning

This lab revolves around using propositional logic to represent knowledge and using automated reasoning to draw
conclusions from that knowledge.
Propositional logic is a formal language that uses symbols to represent statements. These statements can be combined
using logical operators to form more complex statements.

#### Deductive Consequence

Formula G is a deduction or deductive consequence of formulas
F1, F2, . . . , Fn if and only if G can be derived from the premises
F1, F2, . . . , Fn using rules of inference.
We write F1, F2, . . . , Fn ` G and read “F1, . . . , Fn derives or deductively
entails G ”.

#### Soundness and Completeness

An inference rule is sound if, when applied to a set of premises, derives a
formula that is a logical consequence of these premises.
Formally, a rule of inference r is sound if and only if
`if F1, . . . , Fn |- G then F1, . . . , Fn |= G`

A set of rules R is complete if and only if it can be used to derive all
logical consequences:
`if F1, . . . , Fn |= G then F1, . . . , Fn |- G`.

### Resolution method

The resolution method is a complete inference rule for propositional logic. It is based on the resolution principle,
which states that any propositional formula that is a logical consequence of a set of propositional formulas is also a
logical consequence of a subset of that set.

A literal is an atom or its negation. A clause is a disjunction of finitely many literals Gi:
`1 ∨ G2 ∨ · · · ∨ Gn, n ≥ 0`. A clause containing a single literal is called a unit clause.

Every formula in the lab examples is already in CNF (conjunctive normal form), which is a conjunction of clauses.

Direct resolution is incomplete, but refutation is complete. Refutation is the process of showing that a formula is
unsatisfiable by deriving a contradiction from it. We are trying to prove:

`F1 ∧ · · · ∧ Fn ∧ ¬G `

As a special case of the resolution rule we have:
`A ¬A / NIL`.
NIL denotes the empty clause whose semantic value is ⊥.
If the resolution procedure derives NIL, then this means that the
premises are inconsistent (because the resolution rule is sound).
The goal of the lab assignment is to try to prove that a given formula is unsatisfiable by deriving NIL.

#### Refutation resolution algorithm

```
function plResolution(F, G)
    clauses ← cnfConvert(F ∧ ¬G)
    new ← ∅
    loop do
        for each (c1, c2) in selectClauses(clauses) do
            resolvents ← plResolve(c1, c2)
            if NIL ∈ resolvents then return true
            new ← new ∪ resolvents
        if new ⊆ clauses then return false
        clauses ← clauses ∪ new
```

To retain completeness, factorization is applied on each derived resolvent.
Used simplification strategy is a Deletion strategy:
- removal of redundant clauses - a clause that is subsumed by another clause
- removal of irrelevant clauses - tautology clauses

Also, a control resolution strategy is used:
- Set-of-support strategy (SoS)
  - builds on the assumption that the set of input premises is consistent
  - the clauses obtained from the negated goal as well as all subsequently derived clauses
  - at leas one parent clause always comes from the SoS
