export enum DependencyType
{
    SCRIPT = "script",
    STYLESHEET = "stylesheet"
}

export interface Dependency
{
    /* Location of dependency */
    uri: string;
    /* Type of dependency. One of "script", "stylesheet" */
    type: DependencyType;
    /* If applicable (i.e. a script) should this dependency be loaded asynchronously. Defaults to true */
    async?: boolean;
}

export async function loadDependencies(dependencies: Dependency[])
{
    // Get current script and css tags in page
    let currentScripts = Array.from(document.getElementsByTagName("script")).map(elem => { return elem.src; });
    let currentCSS = Array.from(document.getElementsByTagName("link")).map(elem => { return elem.href; })

    for(let dependency of dependencies)
    {
        if(dependency.type == DependencyType.SCRIPT)
        {
            // If the dependency isn't already in the page, add it
            if(!currentScripts.some(url => dependency.uri === url))
            {
                console.log("Loading dependency: " + JSON.stringify(dependency));
                if(dependency.async)
                {
                    loadDependency(dependency).then(() => console.log("Dependency loaded"));
                }
                else
                {
                    await loadDependency(dependency);
                    console.log("Dependency loaded");
                }
            }
        }
        else if(dependency.type == DependencyType.STYLESHEET)
        {
            // If the dependency isn't already in the page, add it
            if(!currentCSS.some(url => dependency.uri === url))
            {
                let css: HTMLLinkElement = document.createElement("link");
                css.rel = "stylesheet";
                css.href = dependency.uri;
                document.head.appendChild(css);
            }
        }
    }
}

async function loadDependency(dependency: Dependency)
{
    return new Promise(((resolve, reject) =>
    {
        let script: HTMLScriptElement = document.createElement("script");
        script.src = dependency.uri;
        script.type = "text/javascript";
        if(dependency.async === false)
        {
            script.async = false;
        }
        document.head.appendChild(script);

        script.onload = resolve;
        script.onerror = reject;
    }));
}