# FRIDAY GUI design

The JavaFX GUI uses a compact, assistant-oriented conversation layout. FRIDAY replies are
wide left-aligned cards with a small circular FRIDAY profile picture, while user commands
are compact right-aligned bubbles with a circular Tony Stark profile picture. User-correctable
errors use a high-contrast red card and warning icon.

The layout is responsive: the conversation scroll pane receives available vertical space,
and message labels wrap to the available width up to 480 pixels. The fixed header and
input bar preserve their natural heights when the window is resized.

The design is implemented with standard JavaFX FXML and CSS controls only. It uses simple
gradients, SVGPath icons, and a single drop shadow; no external font or image dependency is
required.
