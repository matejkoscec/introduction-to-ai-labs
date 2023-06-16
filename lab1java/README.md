# lab 1

## State Space Search

The solution revolves around using the general search algorithm:

```
function search(s0,succ, goal)
    open ← [initial(s0)]
    while open =/= [] do
        n ← removeHead(open)
        if goal(state(n)) then return n
        for m ∈ expand(n,succ) do
            insert(m, open)
    return fail
```

There are different algorithms implemented based on the insertion method used in the open list:

- Blind search algorithms:
  - **BFS** - insert at the end of the list
  - **UCS** - insert in a sorted manner based on the path cost
- Heuristic/Informed search algorithms:
  - **ASTAR** - insert in a sorted manner based on the heuristic value

# Heuristics

The solution also checks if the heuristic is optimistic and consistent.

Heuristic function h is optimistic or admissible iff it never overestimates,
i.e., its value is never greater than the true cost needed to reach the goal:
`∀s∈S. h(s) ≤ h*(s),`
where h*(s) is the true path cost of reaching the goal state from state s.

Heuristic h is consistent or monotone iff:
`∀(s2, c) ∈ succ(s1). h(s1) ≤ h(s2) + c`.
