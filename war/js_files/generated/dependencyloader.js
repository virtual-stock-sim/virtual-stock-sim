var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
export function loadDependencies(dependencies) {
    return __awaiter(this, void 0, void 0, function* () {
        let head = document.getElementsByTagName("head")[0];
        let currentScripts = Array.from(document.getElementsByTagName("script")).map(elem => { return elem.src; });
        let currentCSS = Array.from(document.getElementsByTagName("link")).map(elem => { return elem.href; });
        for (let dependency of dependencies) {
            if (dependency.type === "script") {
                if (!currentScripts.some(url => dependency.uri === url)) {
                    let script = document.createElement("script");
                    script.src = dependency.uri;
                    script.type = "text/javascript";
                    if (dependency.async === false) {
                        script.async = false;
                    }
                    head.appendChild(script);
                }
            }
            else if (dependency.type === "stylesheet") {
                if (!currentCSS.some(url => dependency.uri === url)) {
                    let css = document.createElement("link");
                    css.rel = "stylesheet";
                    css.href = dependency.uri;
                    head.appendChild(css);
                }
            }
        }
    });
}
