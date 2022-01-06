package main

import (
	"archive/zip"
	"fmt"
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/data/binding"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
	"io"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"strings"
	"time"
)

const appName = "Foxxey"
const windowHeight = 500
const windowWidth = 500

var hellos = [...]string{
	"Я готовлю все для запуска Foxxey!",
	"Как насчет чашечки кофе?",
	"Just Viktor",
	"А ты знал, что в лаунчере спрятаны пасхалки?",
	"Какой же все таки у тебя медленный интернет!",
	"Не паникуем, Фоксей знает что делает!",
	"Неужели я снова потерял свои инструменты?",
	"Если бы не эти ваши люди, все было бы хорошо",
	"Главное – ничего не сломать",
	"Может включим музычку? Было бы веселее",
	"Вся проблема в вот этих ваших интернетах!",
	"Я ношу файлы, а ты что делаешь?",
}

func main() {
	mApp := app.New()
	mApp.Settings().SetTheme(foxxeyTheme{})
	mWindow := mApp.NewWindow(appName)
	mWindow.Resize(fyne.Size{
		Height: windowHeight,
		Width:  windowWidth,
	})
	mWindow.SetFixedSize(true)
	bind := binding.NewString()
	topText := widget.NewLabelWithData(bind)
	topText.Alignment = fyne.TextAlignCenter
	go func() {
		for true {
			rand.Seed(time.Now().UnixNano())
			err := bind.Set(hellos[rand.Intn(len(hellos))])
			if err != nil {
				fmt.Println(err)
				return
			}
			time.Sleep(5 * time.Second)
		}
	}()
	progressBar := widget.NewProgressBarInfinite()
	foxxeyImage := canvas.NewImageFromResource(resourceFoxxeyPng)
	sl := container.New(layout.NewBorderLayout(topText, progressBar, nil, nil), topText, progressBar)
	content := container.New(
		layout.NewBorderLayout(
			nil, sl, nil, nil,
		), sl, foxxeyImage,
	)
	mWindow.SetContent(content)
	mWindow.ShowAndRun()
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
