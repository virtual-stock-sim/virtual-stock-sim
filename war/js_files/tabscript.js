function getLinkedPage()
{
    let linkedPage = window.location.href.split("#").slice(-1)[0];
    let prefix = linkedPage.substr(0, 4);
    if(prefix !== 'http' && prefix !== 'file')
    {
        return linkedPage;
    }
    else
    {
        return null;
    }
}

function openTab(tabName)
{
    if(typeof tabName !== 'string')
    {
        console.log(`Parameter(s) for openTab need to be of type 'string'. ${typeof tabName} was given`);
        return;
    }

    for(let tab of document.getElementsByClassName("tab"))
    {
        tab.classList.remove("tab-selected");
    }
    for(let page of document.getElementsByClassName("page"))
    {
        page.classList.remove("page-load");
        page.style.display = "none";
    }

    let tab = document.getElementById(`tab-${tabName}`);
    tab.classList.add("tab-selected");

    let page = document.getElementById(`page-${tabName}`);
    page.classList.add("page-load");
    page.style.display = "inline-block";

    let linkedPage = getLinkedPage();
    if(linkedPage !== null)
    {
        let link = window.location.href.split("#");
        link[link.length-1] = tabName;
        window.location.href = link.join("#");
    }
    else
    {
        window.location.href += `#${tabName}`;
    }

}