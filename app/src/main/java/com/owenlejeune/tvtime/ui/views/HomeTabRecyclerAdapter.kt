package com.owenlejeune.tvtime.ui.views

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class HomeTabRecyclerAdapter: RecyclerView.Adapter<HomeTabRecyclerAdapter.TabViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract, KoinComponent
{

    class TabViewHolder(itemView: ComposeView): RecyclerView.ViewHolder(itemView)

    private val pages: MutableList<HomeScreenNavItem?>
    private val indexOfDivider
        get() = pages.indexOf(null)

    init {
        val visiblePages = HomeScreenNavItem.Items.filter { it.order > -1 }.sortedBy { it.order }
        val hiddenPages = HomeScreenNavItem.Items.filter { it.order == -1 }
        pages = ArrayList<HomeScreenNavItem?>().apply {
            addAll(visiblePages)
            add(null)
            addAll(hiddenPages)
        }
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val composeView = ComposeView(get()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        return TabViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val itemView = holder.itemView
        if (itemView is ComposeView) {
            val page = pages[position]
            itemView.setContent {
                if (page == null) {
                    ItemDivider()
                } else {
                    ItemRow(page = page)
                }
            }
        }
    }
    
    @Composable
    private fun ItemRow(page: HomeScreenNavItem) {
        Row(
            modifier = Modifier
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = page.icon,
                contentDescription = page.name
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = page.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .size(24.dp),
                imageVector = Icons.Filled.DragIndicator,
                contentDescription = null
            )
        }
    }

    @Composable
    private fun ItemDivider() {
        Row(modifier = Modifier.height(50.dp)) {
            Text(
                text = "Hidden",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp)
            )
            Divider(
                modifier = Modifier.align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onBackground,
                thickness = 2.dp
            )
        }
    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder) {
        myViewHolder.itemView.alpha = 1f
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (indexOfDivider == 1 && toPosition > 1) {
            return
        }
        pages.add(
            toPosition,
            pages.removeAt(fromPosition)
        )
        pages.forEachIndexed { index, bottomNavItem ->
            bottomNavItem?.order = if (index > indexOfDivider) -1 else index
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder) {
        myViewHolder.itemView.alpha = 0.6f
    }

}