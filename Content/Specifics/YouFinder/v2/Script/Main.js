let GBase = null,
	MBase = null,

	UserInfo = null;

DOM.importAPI("https://genbuproject.github.io/Programs/Sync Helper/Sync Helper v1.1.js", function () {
	GBase = new GoogleAPI({
		ID: "239141875067-k7ftnrifgiv328ai7j0nnec8s79pjlro.apps.googleusercontent.com",
		Key: atob("Z21COW1NOWVxVXhCOHRqNVVBSWZIeThf"),

		Url: "http://localhost:8001/Content/Specifics/YouFinder/v2/"
	});

	(function () {
		let Timer = setInterval(function () {
			if (GBase.hasLogined()) {
				MBase = new GBase.GmailAPI(true);
				UserInfo = GBase.getUserInfo();

				clearInterval(Timer);
			}
		}, 200);
	})();
});