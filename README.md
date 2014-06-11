FeatureOverlayComponent
=======================

The Feature Overlay Component is designed to highlight and showcase specific parts of apps to the user with a description text and a cutout circle on top of the component. 

The Cutout Feature can be used as follows:

ViewTarget target = new ViewTarget("your view's id", this);
        
        cutoutView = new CutoutView.Builder(this, true)
                .setTarget(target)
                .setContentTitle(R.string.cutout_main_title)
                .setContentText(R.string.cutout_main_message)
                .setStyle(R.style.CustomOverlayTheme)
                .setCutoutEventListener(this)
                .setShdCenterText(false)
                .setOnClickListener(this)
                .build();
