package main

import (
	"archive/zip"
	"errors"
	"fmt"
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/data/binding"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"
	"io"
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"time"
)

const appName = "Foxxey"
const windowHeight = 500
const windowWidth = 500
const foxesWorldPath = "/foxesworld"
const foxxeyPath = "/foxxey"
const jreUrl = "https://foxesworld.ru:8080/api/jre?os=" + runtime.GOOS
const runtimeDirName = "runtime"

var appDir string

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
	"Вперед и с песней!",
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
	go background(mApp)
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

func resolveAppDir() {
	appDir, _ = os.UserConfigDir()
	appDir += foxesWorldPath + foxxeyPath
	_ = os.MkdirAll(appDir, 0777)
}

func background(app fyne.App) {
	resolveAppDir()
	downloadAndUnzipJreIfNotExists()
	app.Quit()
}

func downloadAndUnzipJreIfNotExists() {
	jrePath := appDir + "/" + runtimeDirName
	if _, err := os.Stat(jrePath); err == nil {
		log.Println("JRE is downloaded")
		return
	}
	log.Println("JRE isn't downloaded. Downloading..")
	jreZipPath := appDir + "/jre.zip"
	downloadJre(jreZipPath)
	unzipToAppDir(jreZipPath)
	logFatalIfError(os.Remove(jreZipPath))
	renameJreDir()
	log.Println("JRE successfully downloaded")
}

func renameJreDir() {
	jreDirName, err := findJreDirAndGetName()
	logFatalIfError(err)
	jreDirPath := appDir + "/" + jreDirName
	logFatalIfError(os.Rename(jreDirPath, appDir+"/"+runtimeDirName))
}

func findJreDirAndGetName() (string, error) {
	dir, err := ioutil.ReadDir(appDir)
	logFatalIfError(err)
	for _, item := range dir {
		if item.IsDir() {
			dirName := item.Name()
			if strings.Contains(dirName, "jre") {
				return item.Name(), nil
			}
		}
	}
	return "", errors.New("jre dir not found")
}

func unzipToAppDir(zipPath string) {
	logFatalIfError(unzipSource(zipPath, appDir))
}

func downloadJre(jreZipPath string) {
	logFatalIfError(DownloadFile(jreZipPath, jreUrl))
}

func logFatalIfError(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func unzipSource(source, destination string) error {
	reader, err := zip.OpenReader(source)
	if err != nil {
		return err
	}
	defer func(reader *zip.ReadCloser) {
		_ = reader.Close()
	}(reader)

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
	defer func(destinationFile *os.File) {
		_ = destinationFile.Close()
	}(destinationFile)

	zippedFile, err := f.Open()
	if err != nil {
		return err
	}
	defer func(zippedFile io.ReadCloser) {
		_ = zippedFile.Close()
	}(zippedFile)

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
	defer func(Body io.ReadCloser) {
		_ = Body.Close()
	}(resp.Body)

	out, err := os.Create(filepath)
	if err != nil {
		return err
	}
	defer func(out *os.File) {
		_ = out.Close()
	}(out)

	_, err = io.Copy(out, resp.Body)
	return err
}
