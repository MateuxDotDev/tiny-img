package options

type QueuePayload struct {
	OriginalImagePath string `json:"originalImagePath"`
	User              string `json:"user"`
	ImageID           string `json:"imageId"`
	Size              int    `json:"size"`
	Format            string `json:"format"`
	Quality           int    `json:"quality"`
}
