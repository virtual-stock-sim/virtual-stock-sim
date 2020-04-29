export interface Dependency
{
    /* Location of dependency */
    uri: string;
    /* Type of dependency. One of "script", "stylesheet" */
    type: string;
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
        if(dependency.type === "script")
        {
            // If the dependency isn't already in the page, add it
            if(!currentScripts.some(url => dependency.uri === url))
            {
                let script: HTMLScriptElement = document.createElement("script");
                script.src = dependency.uri;
                script.type = "text/javascript";
                if(dependency.async === false)
                {
                    script.async = false;
                }
                document.head.appendChild(script);
            }
        }
        else if(dependency.type === "stylesheet")
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