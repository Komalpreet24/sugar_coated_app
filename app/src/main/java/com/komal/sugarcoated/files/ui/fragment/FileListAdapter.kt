package com.komal.sugarcoated.files.ui.fragment


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.komal.sugarcoated.R
import com.komal.sugarcoated.files.model.FileData
import com.komal.sugarcoated.files.ui.vm.FileClickListener
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer


class FileListAdapter(private val fileList: List<FileData>,
                      private val fileClickListener: FileClickListener
) :
  RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val thumbnail: ImageView = itemView.findViewById(R.id.file_icon)
    val fileName: TextView = itemView.findViewById(R.id.tv_file_name)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.file_item_layout, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val file = fileList[position]

    val thumbnailBitmap = generatePdfThumbnail(holder.itemView.context, Uri.parse(file.url), 0)
    if (thumbnailBitmap != null) {
      holder.thumbnail.setImageBitmap(thumbnailBitmap)
    } else {
      holder.thumbnail.setImageResource(R.drawable.ic_files)
    }

    holder.fileName.text = file.name

    holder.itemView.setOnClickListener {
      fileClickListener.onFileClicked(file)
    }
  }

  override fun getItemCount(): Int = fileList.size

  fun generatePdfThumbnail(context: Context, uri: Uri, pageNumber: Int): Bitmap? {
    try {
      val inputStream = context.contentResolver.openInputStream(uri)
      val pdfDocument = PDDocument.load(inputStream)
      val pdfRenderer = PDFRenderer(pdfDocument)
      val page = pdfRenderer.renderImage(pageNumber)
      pdfDocument.close()
      return page as Bitmap
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }



}

