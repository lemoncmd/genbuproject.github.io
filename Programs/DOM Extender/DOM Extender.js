(function () {
    let Info = (function () {
        let VGetter = new XMLHttpRequest();
            VGetter.open("GET", "API.Info", false);
            VGetter.send(null);

        return JSON.parse(VGetter.response);
    })();

    let SGetter = new XMLHttpRequest();
        SGetter.open("GET", Info.Name + " " + Info.Version + ".js", false);
        SGetter.send(null);

    eval(SGetter.response);
})();