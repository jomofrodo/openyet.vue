UPDATE combined
SET statecode = state.ansi
FROM state
WHERE combined.state = state.name;