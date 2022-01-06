package main

import (
	"archive/zip"
	"fmt"
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"strings"
)

const appName = "Foxxey"
const windowHeight = 500
const windowWidth = 500

func main() {
	fmt.Println("Started.")
	fmt.Println("Creating app..")
	mApp := app.New()
	fmt.Println("App created. Creating window..")
	mWindow := mApp.NewWindow(appName)
	fmt.Println("Window created. Configuring..")
	mWindow.Resize(fyne.Size{
		Height: windowHeight,
		Width:  windowWidth,
	})
	mWindow.SetFixedSize(true)
	fmt.Println("Configured. Adding content..")
	progressBar := widget.NewProgressBarInfinite()
	content := container.New(
		layout.NewBorderLayout(
			nil, progressBar, nil, nil,
		), progressBar,
	)
	mWindow.SetContent(content)
	fmt.Println("Content added. Starting background process..")
	go background(mWindow)
	fmt.Println("Show and run..")
	mWindow.ShowAndRun()
	fmt.Println("Stopped.")
}

func background(window fyne.Window) {
	fmt.Println("Background started.")
}

func unzipSource(source, destination string) error {
	reader, err := zip.OpenReader(source)
	if err != nil {
		return err
	}
	defer reader.Close()

	destination, err = filepath.Abs(destination)
	if err != nil {
		return err
	}

	for _, f := range reader.File {
		err := unzipFile(f, destination)
		if err != nil {
			return err
		}
	}

	return nil
}

func unzipFile(f *zip.File, destination string) error {
	filePath := filepath.Join(destination, f.Name)
	if !strings.HasPrefix(filePath, filepath.Clean(destination)+string(os.PathSeparator)) {
		return fmt.Errorf("invalid file path: %s", filePath)
	}

	if f.FileInfo().IsDir() {
		if err := os.MkdirAll(filePath, os.ModePerm); err != nil {
			return err
		}
		return nil
	}

	if err := os.MkdirAll(filepath.Dir(filePath), os.ModePerm); err != nil {
		return err
	}

	destinationFile, err := os.OpenFile(filePath, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, f.Mode())
	if err != nil {
		return err
	}
	defer destinationFile.Close()

	zippedFile, err := f.Open()
	if err != nil {
		return err
	}
	defer zippedFile.Close()

	if _, err := io.Copy(destinationFile, zippedFile); err != nil {
		return err
	}
	return nil
}

func DownloadFile(filepath string, url string) error {

	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	out, err := os.Create(filepath)
	if err != nil {
		return err
	}
	defer out.Close()

	_, err = io.Copy(out, resp.Body)
	return err
}
