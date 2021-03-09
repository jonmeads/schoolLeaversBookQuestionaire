matchMedia("(prefers-color-scheme: dark)").addEventListener("change", applySystemTheme);

function applySystemTheme() {
    let theme = matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    document.documentElement.setAttribute("theme", theme);
}

applySystemTheme();