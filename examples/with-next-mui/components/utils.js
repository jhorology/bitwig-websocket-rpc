export function capitalize(string) {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

export function round(num, digits) {
  const d = 10 ** digits
  return Math.round(num * d) / d
}
